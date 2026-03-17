# MinehutEssentials

A Client-Side fabric mod for making Minehut bearable.
## Features

| Feature                  | What it blocks                                                 | Screenshot                                                                              |
|--------------------------|----------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| Ad block                 | Messages containing `[AD]`                                     | ![Ad block](screenshots/advert.png)                                                     |
| Boop time                | "just reached X minutes in the Lobby X Boop Arena" announcements | ![Boop time](screenshots/boop.png)                                                      |
| Boop Arena welcome       | The Boop Arena welcome message                                 | ![Boop Arena welcome](screenshots/arena.png)                                            |
| Lobby join notice        | "USERNAME joined your lobby." messages                         | ![Lobby join notice](screenshots/lobbyjoined.png)                                       |
| Minehut banner messages  | Banner-style messages with `--------[MINEHUT]--------`         | ![Minehut banner](screenshots/minehutbanner.png)                                        |
| Raid countdown bossbar   | Raid starts in X Seconds bossbar                               | ![Raid countdown bossbar](screenshots/bossbar.png) |
| Raid ready               | "A new raid is ready! Enter the circle to begin."              | ![Raid ready](screenshots/raid.png)                                                     |
| Rules reminder           | Rules reminder messages (includes `/rules`)                    | ![Rules reminder](screenshots/reminder.png)                                             |
| Vote reward announcement | Vote reward announcements (`...by voting via /vote`)           | ![Vote reward announcement](screenshots/vote.png)                                       |


## Todo / Fixes
- [ ] Add intermediate skipping by automatically using the barrier item to get to the lobby or desired server

## Usage

- `/mhessentials config` - Open the in-chat blocklist panel.
- Click `[Enable]` / `[Disable]` in the panel to toggle each filter.
- Mod Menu integration to easily toggle each filter 
- Each block is enabled by default

## Issues \& Pull Requests

\- Found a bug or problem? Open an issue: <https://github.com/NavaShield/MinehutEssentials/issues>  
\- Want to contribute a fix or improvement? Open a pull request: <https://github.com/NavaShield/MinehutEssentials/pulls>

## Compiling

1. Clone the repository:
   `git clone https://github.com/NavaShield/MinehutEssentials.git`
2. Enter the project folder:
   `cd MinehutEssentials`
3. Build with Gradle:
   `./gradlew build`
4. Find the compiled `.jar` in:
   `build/libs/`