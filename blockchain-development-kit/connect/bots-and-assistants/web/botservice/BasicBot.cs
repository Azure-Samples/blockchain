// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

// See https://github.com/microsoft/botbuilder-samples for a more comprehensive list of samples.

using System;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Bot.Builder;
using Microsoft.Bot.Builder.Dialogs;
using Microsoft.Bot.Schema;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;

namespace Microsoft.BotBuilderSamples
{
    /// <summary>
    /// Main entry point and orchestration for bot.
    /// </summary>
    public class BasicBot : IBot
    {
        // Supported LUIS Intents
        public const string MarketplaceIntent = "Marketplace";

        /// <summary>
        /// Key in the bot config (.bot file) for the LUIS instance.
        /// In the .bot file, multiple instances of LUIS can be configured.
        /// </summary>
        public static readonly string LuisConfiguration = "blockchainbot6_marketplace";

        private readonly IStatePropertyAccessor<BlockchainState> _blockchainStateAccessor;
        private readonly IStatePropertyAccessor<DialogState> _dialogStateAccessor;
        private readonly UserState _userState;
        private readonly ConversationState _conversationState;
        private readonly BotServices _services;

        /// <summary>
        /// Initializes a new instance of the <see cref="BasicBot"/> class.
        /// </summary>
        /// <param name="botServices">Bot services.</param>
        /// <param name="accessors">Bot State Accessors.</param>
        public BasicBot(BotServices services, UserState userState, ConversationState conversationState, ILoggerFactory loggerFactory)
        {
            _services = services ?? throw new ArgumentNullException(nameof(services));
            _userState = userState ?? throw new ArgumentNullException(nameof(userState));
            _conversationState = conversationState ?? throw new ArgumentNullException(nameof(conversationState));

            _blockchainStateAccessor = _userState.CreateProperty<BlockchainState>(nameof(BlockchainState));
            _dialogStateAccessor = _conversationState.CreateProperty<DialogState>(nameof(DialogState));

            // Verify LUIS configuration.
            if (!_services.LuisServices.ContainsKey(LuisConfiguration))
            {
                throw new InvalidOperationException($"The bot configuration does not contain a service type of `luis` with the id `{LuisConfiguration}`.");
            }

            Dialogs = new DialogSet(_dialogStateAccessor);
            Dialogs.Add(new BlockchainDialog(_blockchainStateAccessor, services, loggerFactory));
        }

        private DialogSet Dialogs { get; set; }

        /// <summary>
        /// Run every turn of the conversation. Handles orchestration of messages.
        /// </summary>
        /// <param name="turnContext">Bot Turn Context.</param>
        /// <param name="cancellationToken">Task CancellationToken.</param>
        /// <returns>A <see cref="Task"/> representing the asynchronous operation.</returns>
        public async Task OnTurnAsync(ITurnContext turnContext, CancellationToken cancellationToken)
        {
            Activity activity = turnContext.Activity;

            // Create a dialog context
            DialogContext dc = await Dialogs.CreateContextAsync(turnContext);
            if (activity.Type == ActivityTypes.Message)
            {
                // Perform a call to LUIS to retrieve results for the current activity message.
                RecognizerResult luisResults = await _services.LuisServices[LuisConfiguration].RecognizeAsync(dc.Context, cancellationToken).ConfigureAwait(false);

                // If any entities were updated, treat as interruption.
                // For example, "no my name is tony" will manifest as an update of the name to be "tony".
                (string intent, double score)? topScoringIntent = luisResults?.GetTopScoringIntent();

                string topIntent = topScoringIntent.Value.intent;

                // update greeting state with any entities captured
                await UpdateBlockchainState(luisResults, dc.Context);
                
                // Continue the current dialog
                DialogTurnResult dialogResult = await dc.ContinueDialogAsync();

                // if no one has responded,
                if (!dc.Context.Responded)
                {
                    // examine results from active dialog
                    switch (dialogResult.Status)
                    {
                        case DialogTurnStatus.Empty:
                            switch (topIntent)
                            {
                                case MarketplaceIntent:
                                    await dc.BeginDialogAsync(nameof(BlockchainDialog));
                                    break;
                                default:
                                    // Help or no intent identified, either way, let's provide some help.
                                    // to the user
                                    await dc.Context.SendActivityAsync("I didn't understand what you just said to me.");
                                    break;
                            }

                            break;

                        case DialogTurnStatus.Waiting:
                            // The active dialog is waiting for a response from the user, so do nothing.
                            break;

                        case DialogTurnStatus.Complete:
                            await dc.EndDialogAsync();
                            break;

                        default:
                            await dc.CancelAllDialogsAsync();
                            break;
                    }
                }
            }
            await _conversationState.SaveChangesAsync(turnContext);
            await _userState.SaveChangesAsync(turnContext);
        }

        /// <summary>
        /// Helper function to update greeting state with entities returned by LUIS.
        /// </summary>
        /// <param name="luisResult">LUIS recognizer <see cref="RecognizerResult"/>.</param>
        /// <param name="turnContext">A <see cref="ITurnContext"/> containing all the data needed
        /// for processing this conversation turn.</param>
        /// <returns>A task that represents the work queued to execute.</returns>
        private async Task UpdateBlockchainState(RecognizerResult luisResult, ITurnContext turnContext)
        {
            if (luisResult.Entities != null && luisResult.Entities.HasValues)
            {
                // Get latest GreetingState
                await _blockchainStateAccessor.DeleteAsync(turnContext);
                BlockchainState blockchainState = await _blockchainStateAccessor.GetAsync(turnContext, () => new BlockchainState());
                
                JObject entities = luisResult.Entities;

                // Supported LUIS Entities
                string[] askingPriceEntities = { "askingprice" };
                string[] descriptionEntities = { "description" };

                // Update any entities
                // Note: Consider a confirm dialog, instead of just updating.
                foreach (string askingPriceEntity in askingPriceEntities)
                {
                    // Check if we found valid slot values in entities returned from LUIS.
                    if (entities[askingPriceEntity] != null)
                    {
                        string askingPrice = (string)entities[askingPriceEntity][0];
                        blockchainState.AskingPrice = int.Parse(askingPrice);
                        break;
                    }
                }

                foreach (string descriptionEntity in descriptionEntities)
                {
                    if (entities[descriptionEntity] != null)
                    {
                        blockchainState.Description = (string)entities[descriptionEntity][0];
                        break;
                    }
                }

                // Set the new values into state.
                await _blockchainStateAccessor.SetAsync(turnContext, blockchainState);
            }
        }
    }
}
