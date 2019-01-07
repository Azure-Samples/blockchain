// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

using System;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Bot.Builder;
using Microsoft.Bot.Builder.Dialogs;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace Microsoft.BotBuilderSamples
{

    public class BlockchainState
    {
        public string Description { get; set; }
        public int AskingPrice { get; set; }
    }
    public class MarketplaceRequest
    {
        public string Description { get; set; }
        public int AskingPrice { get; set; }
        public string Email { get; set; }
    }

    /// <summary>
    /// Demonstrates the following concepts:
    /// - Use a subclass of ComponentDialog to implement a multi-turn conversation
    /// - Use a Waterflow dialog to model multi-turn conversation flow
    /// - Use custom prompts to validate user input
    /// - Store conversation and user state.
    /// </summary>
    public class BlockchainDialog : ComponentDialog
    {
        private readonly BotServices _botServices;

        // Dialog IDs
        private const string ProfileDialog = "profileDialog";

        /// <summary>
        /// Initializes a new instance of the <see cref="GreetingDialog"/> class.
        /// </summary>
        /// <param name="botServices">Connected services used in processing.</param>
        /// <param name="botState">The <see cref="UserState"/> for storing properties at user-scope.</param>
        /// <param name="botServices"></param>
        /// <param name="loggerFactory">The <see cref="ILoggerFactory"/> that enables logging and tracing.</param>
        public BlockchainDialog(IStatePropertyAccessor<BlockchainState> userProfileStateAccessor, BotServices botServices, ILoggerFactory loggerFactory)
            : base(nameof(BlockchainDialog))
        {
            _botServices = botServices;
            UserProfileAccessor = userProfileStateAccessor ?? throw new ArgumentNullException(nameof(userProfileStateAccessor));

            // Add control flow dialogs
            WaterfallStep[] waterfallSteps = new WaterfallStep[]
            {
                DisplayMarketplaceStateStepAsync,
            };
            AddDialog(new WaterfallDialog(ProfileDialog, waterfallSteps));
        }

        public IStatePropertyAccessor<BlockchainState> UserProfileAccessor { get; }

        private async Task<DialogTurnResult> DisplayMarketplaceStateStepAsync(
                                                    WaterfallStepContext stepContext,
                                                    CancellationToken cancellationToken)
        {
            return await SellOnMarketplace(stepContext);
        }


        // Helper function to greet user with information in GreetingState.
        private async Task<DialogTurnResult> SellOnMarketplace(WaterfallStepContext stepContext)
        {
            ITurnContext context = stepContext.Context;
            BlockchainState greetingState = await UserProfileAccessor.GetAsync(context);
            HttpClient httpClient = new HttpClient
            {
                BaseAddress = new Uri(_botServices.BlockchainEndpoint),
            };
            HttpResponseMessage response = await httpClient.PostAsync("", new StringContent(JsonConvert.SerializeObject(new MarketplaceRequest()
            {
                Description = greetingState.Description,
                AskingPrice = greetingState.AskingPrice,
                Email = "mymail@example.com",
            })));
            string result = await response.Content.ReadAsStringAsync();
            // Display their profile information and end dialog.
            await context.SendActivityAsync($"Your item is now for sale!");
            
            return await stepContext.EndDialogAsync();
        }
    }
}
