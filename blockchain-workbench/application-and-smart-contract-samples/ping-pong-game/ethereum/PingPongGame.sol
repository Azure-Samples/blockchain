pragma solidity ^0.4.25;

contract Starter
{
    enum StateType { GameProvisioned, Pingponging, GameFinished}

    StateType public State;

    string public PingPongGameName;
    address public Starter;
    address public GamePlayer;
    int public PingPongTimes;

    constructor (string gameName) {
        PingPongGameName = gameName;
        Starter = msg.sender;

        GamePlayer = new Player(PingPongGameName);

        State = StateType.GameProvisioned;
    }

    function StartPingPong(int pingPongTimes)
    {
        PingPongTimes = pingPongTimes;

        Player player = Player(GamePlayer);
        State = StateType.Pingponging;

        player.Ping(pingPongTimes);
    }

    function Pong(int currentPingPongTimes)
    {
        currentPingPongTimes = currentPingPongTimes - 1;

        Player player = Player(GamePlayer);
        if(currentPingPongTimes > 0)
        {
            State = StateType.Pingponging;
            player.Ping(currentPingPongTimes);
        }
        else
        {
            State = StateType.GameFinished;
            player.FinishGame();
        }
    }

    function FinishGame()
    {
        State = StateType.GameFinished;
    }
}

contract Player
{
    enum StateType {PingpongPlayerCreated, PingPonging, GameFinished}

    StateType public State;

    address public GameStarter;
    string public PingPongGameName;

    constructor (string pingPongGameName) {
        GameStarter = msg.sender;
        PingPongGameName = pingPongGameName;

        State = StateType.PingpongPlayerCreated;
    }

    function Ping(int currentPingPongTimes)
    {
        currentPingPongTimes = currentPingPongTimes - 1;

        Starter starter = Starter(msg.sender);
        if(currentPingPongTimes > 0)
        {
            State = StateType.PingPonging;
            starter.Pong(currentPingPongTimes);
        }
        else
        {
            State = StateType.GameFinished;
            starter.FinishGame();
        }
    }

    function FinishGame()
    {
        State = StateType.GameFinished;
    }
}
