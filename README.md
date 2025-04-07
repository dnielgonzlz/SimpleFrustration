# Simple Frustration Board Game

This is a simulation of a game called "Simple Frustration". I was not familiarised with the name of this game in English, but I knew it was 'Ludo' in Spanish.

In the beginning, the initial understanding of the task was straightforward, but then I realised I was getting confused with the logic of the increasing of the positions depending on the positions of the Players on the board, and what size the Board was.

It took me a few days to realise that I misread the documentation of the assignment and I realised that the entry position was established in the requirements, and that the Tail Entry position was simply *Home - 1*. 

My second biggest mistake, was that I thought this game was going to be a CLI game, but then I talked to other students and they told me the game needed to be a simulation, so I re-structured the Main code so that it runs based on the setup the code there has, rather than asking for the input of the user to run the simulation. I think this variable made more sense as a game, but I also understand that the goal of this assignment is to implement Software Architecture Design, not to develop a Game.

My way of developing the game was develop the logic in my own way of thinking first, and then apply adjustments based on the patters that we had studied during the weeks of the course. I was also working on a few side projects using Typescript, React Native, Next and Python, which also allowed me to apply these principles into those projects, instead of only focusing on the Simple Frustration game, since I understand concepts better when I have to do something that I decide to do, rather than working on a project thought by someone else (I love building my own ideas).

I also managed to implement all the advanced features of the game, struggling the most with the 'Undo' feature in combination with the 'Hit Rule'. I had a bug that it was saving the State of the game before the Hit happened, and it returned the *hitted* player to Home, but it maintained the position of the Player that did the hit for the next turn, so the Player got "an extra turn", which was obviosly not right. I managed to fix it by simply saving the state before any of the rolls happen.

PS: I know it's a bit unnecessary but I also added a 500ms delay between the turns, so it just doesn't splurt out all the simulation in a short amount of time and it seems a bit more "natural".

## Game Rules

### Basic Game
- Players start at their home positions and attempt to reach the end position first.
- Players roll dice on their turn and move clockwise around the board.
- When a player reaches the position just before their home, they divert up a "tail" towards the end position.
- The first player to reach their end position wins.

## Variations Implemented

This implementation includes all the required variations:

### 1. Player must land exactly on the END position to win
If a player's dice roll would take them beyond the END position, they "bounce back" from the end, moving backwards by the excess distance. This creates a more challenging endgame where players must roll the exact number needed to win.

### 2. Player will be sent HOME when HIT
When a player lands on a position occupied by another player, the player who was there first is sent back to their home position. This creates more strategic gameplay with opportunities to set back opponents.

### 3. Single Die
Players can choose to play with a single 6-sided die instead of two dice. This creates a more controlled game with smaller movements.

### 4. Large Board
The game supports both the standard board (18 main positions, 3 tail positions) and a larger board (36 main positions, 6 tail positions). The larger board creates a longer game with more strategic possibilities.

## Advanced Features

### Four Players
The game supports both 2-player games (Red and Blue) and 4-player games (Red, Blue, Green, and Yellow). Home positions are adjusted based on the board size:
- Basic board: Positions 1, 5, 10, and 14
- Large board: Positions 1, 10, 19, and 28

### Undo
Players can undo their last move, allowing them to take back a dice roll and try again. The undo feature properly restores the game state, including handling the reversal of "hit" events.

## Key Classes and Their Responsibilities

### Board Package
- **IBoard**: Interface defining the board functionality
- **BasicBoard**: Implementation of a basic 18-position board 
- **LargeBoard**: Implementation of a large 36-position board

### Dice Package
- **Dice**: Interface for dice behavior
- **SingleDie**: Implementation of a single 6-sided die
- **TwoDice**: Implementation of two 6-sided dice

### Players Package
- **Player**: Represents a player with position and movement
- **PlayerColor**: Value Object for the Color of the player (being completely honest, I still don't quite understand the practical usage of Value Objects, but it was the only thing I could think of to make into a Value Object. I suppose it's a similar use case of types in Typescript).
- **PlayerManager**: Manages all players and turn order

### Rules Package
- **RuleStrategy**: Interface for game rules behavior
- **BasicRule**: Default rules implementation
- **ExactEndRule**: Decorator adding exact-end requirements
- **HitHomeRule**: Decorator adding hit-home behavior

### Observers Package
- **GameObserver**: Interface for observing game events
- **ConsoleObserver**: Outputs game events to the console

### Game Package
- **Game**: Main game logic and flow controller
- **GameConfig**: Stores game configuration settings

### Undo Package
- **GameStateMemento**: Stores a snapshot of game state
- **GameHistory**: Manages the history of game states

### Factories Package
- **BoardFactory**: Creates appropriate board instances
- **DiceFactory**: Creates appropriate dice instances
- **PlayerFactory**: Creates player instances with correct positions

## Design Patterns Used

### Factory Pattern
Used in BoardFactory, DiceFactory, and PlayerFactory to create instances based on configuration parameters. This encapsulates object creation logic, allowing for easy modification of implementation details.

### Strategy Pattern
Used for the game rules through the RuleStrategy interface. Different rule implementations can be swapped at runtime without affecting the core game logic.

### Decorator Pattern
Used in the rules package to layer rule behavior. ExactEndRule and HitHomeRule both decorate a base rule, adding their behavior while preserving the original functionality.

### Observer Pattern
Used to notify interested parties about game events like moves, hits, and wins. The ConsoleObserver prints these events to the console.

### Memento Pattern
Used in the undo functionality to capture and restore game state without violating encapsulation. GameStateMemento stores the state and GameHistory manages these states.
This is the only one that I managed to find online that we not learned in class, but I was researching into different patterns while I was looking into how to solve for the State problem.

I found this resource that I found quite helpful on how to design this kind of features.

https://refactoring.guru/design-patterns/memento

## Implementation Details

### Game Abstraction
The game is designed with a clear separation of concerns, allowing each component to focus on a specific responsibility:
- The Board knows about positions and movement rules
- The Players know about their state and positions
- The Rules determine movement and hit behavior
- The Game coordinates all these elements

### Extensibility
The code is designed to be easily extensible:
- New board types can be added by implementing IBoard
- New dice types can be added by implementing Dice
- New rule variations can be added as decorators around existing rules
- New observers can be added to provide different outputs

### Console Output
The game provides detailed console output for each turn, showing:
- The current player and dice roll
- Player movements from old to new positions
- Hit events and their consequences
- Win announcements and game statistics

## Final Thoughts

I tried to make this game as over-engineered as possible to showcase my skills on designing software, and instead of keeping my original CLI structure for the assignment, I pivoted in a different way.

Writing this project not only made me aware that there are different patterns I can follow when writing code, but that you can also change the color of your terminal output! Genuinely I'm still shocked by such a small thing haha. It was something I needed to figure out in order to visualize the players process, since without a GUI it's hard for me to see the movements happening, and it got pretty hard then it was with 4 players and I was making sure the rules were working properly.

I think the hardest part of the game was handling state depending on the rules that are being used, but I am quite happy with the structure of this code.

I hope you like this code!

PS: I left all the debugging on to make the game easier to read and understand. I did not did any tests because I am not good with them yet, but I've gotten pretty good at adding console logs (or in this case, print.ln) around the code to make sure the flow of data is working properly.
