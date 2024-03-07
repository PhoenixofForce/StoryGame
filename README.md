# The Story Game

This is an interactive game where you complete each other's stories. *The catch*: You can only see the last sentence the person before you typed.

### Technical Overview
The Frontend shows the current state of the game and sends user inputs to the backend via websockets. The backend handles all the game logic and sends the always changing game state via those sockets.

## Setup

This Project requires
- Java 21
- npm 9.6.4-ish

and uses 
- [Spring](https://spring.io) as a backend framework
- [Lombok](https://projectlombok.org) to avoid writing getter and setter
- [Svelte](https://svelte.dev) as a frontend framework
- [Tailwind](https://tailwindcss.com) for fancy css classes.

### Setup Backend
todo

When in doubt, run maven install.
```bash
.\mvnw clean install
```

Set the dev-dev as an environment variable 
- in your run configuration `spring.profiles.active=dev` or
- as an JVM parameter `-Dspring.profiles.active=dev`

### Setup Frontend

Move to the ui folder
```bash
cd src_ui
```

Install the dependencies
```bash
npm install
```

And now you can run the UI by using the following command:

```bash
npm run dev
```

When changing a file, the page gets rebuild automatically and shows you the new version.

## Building & Deployment
todo 

## Contributing

- If you want to **report a bug** or **request a feature** simply open an issue. Please use the appropriate label bug/enhancement
- If you want to **contribute code** open a Pull Request (to the currently non-existent develop branch) with a meaningful description. Please refrain from opening a pull request that only contains cosmetic changes.

### Loose Code Guidelines
- Use early returns/ guard clauses when applicable
- Don't branch ~too~ deep
- DRY for big pieces of code
- Only repeat smaller pieces of code a few times

### What can be done
(todo: create project page?)

The abbreviation **(BE)** means that something can purely be implemented in the backend with no frontend requirements. <br> 
**(N2H)** means that something is nice to have and no necessity for the first release.

- General
    - Fun Colors/ Design
    - Github workflow that builds the frontend and pushes it to the (currently not existing) gh-pages branch, and also builds docker images for both back- and frontend. Those are published *somewhere currently undecided*
    - (BE) handle connections => when all players disconnected close room
- Start screen, where
  - the player can create a new or join an existing game
  - (BE) Generate a (not yet existing) room-code when omitted
  - (N2H) select the profile picture
  - how to play section
- Lobby screen, where 
  - the room-creator can change settings, which are displayed for the other
  - amount of "rounds"
  - the people that have connected to the lobby
  - copy a link to the room that can be shared
  - timer length, when does the timer start
- Game screen, which shows 
  - the last sentence of the player before you, 
  - the amount of players that are ready
  - the timer and the round
  - (Backend) A good algorithm that distributes the player order well
- Results screen, where the host can
  - reveal each part of the story one by one, TTS can read it out loud
  - text messaging app style-ish
  - (N2H) a voting mechanism 
- Summary screen
  - Overview of all the stories
  - (N2H) with accumulated points
  - possibility of downloading them all
- Nice to have
  - set of fixed images that the players can choose as a profile picture
  - Background music and sounds (that can be muted)
  - dark mode/ light mode toggle
  - Cool, fitting logo and name
  - Minigame for the people that have completed their part of the story
  - A scoring system
  - i18n
  - Use OpenApi to generate Message classes from yaml

## License
todo