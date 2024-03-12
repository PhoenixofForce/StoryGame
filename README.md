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
todo: when tls is working

### Building Frontend

Depending on how you want to deploy the build page, you either have to adjust `base` inside `src_ui\vite.config.ts` or adjust the paths to the js and css files in the build html manually.
Also adjust `src_ui\.env.production` to your needs.

```bash
cd src_ui
npm install
npm run build
```

This generated `src_ui\dist` folder, which contains the static html file.
The easiest way to deploy this is to run
```bash
python3 -m http.server
```

## Contributing

- If you want to **report a bug** or **request a feature** simply open an issue. Please use the appropriate label bug/enhancement
- If you want to **contribute code** open a Pull Request (to the currently non-existent develop branch) with a meaningful description. Please refrain from opening a pull request that only contains cosmetic changes.

### Loose Code Guidelines
- Use early returns/ guard clauses when applicable
- Don't branch ~too~ deep
- DRY for big pieces of code
- Only repeat smaller pieces of code a few times

### What can be done
See [on the project tab](https://github.com/users/PhoenixofForce/projects/2) whats left to do.


## License
todo