<script lang="ts">
  import { connect, addEventHandler } from "../services/gameservice";

  let username = "";
  let roomCode = "";

  let errorMessage = "";

  $: canJoinGame = username.length > 0 && roomCode.length > 0;
  $: canCreateGame = username.length > 0;

  addEventHandler("join", {
    onError: handleJoinError,
  });

  function handleJoinError(error: any) {
    console.log("an error while joining occured", error);
    errorMessage = error.message;
  }

  function connectToSocket(type: "join" | "create") {
    errorMessage = "";
    connect(username, roomCode, type);
  }
</script>

<div>
  <div class="text-5xl mb-4 font-bold tracking-wide drop-shadow">
    The Story Game
  </div>
  <p class="mb-60 italic text-slate-400">
    ~ This site is currently under construction ~
  </p>

  <form on:submit|preventDefault={() => {}}>
    <input bind:value={username} placeholder="Username" />
    <input bind:value={roomCode} placeholder="Room Code" />
    <div class="buttons">
      <button on:click={() => connectToSocket("join")} disabled={!canJoinGame}
        >Join Game</button
      >
      <button
        on:click={() => connectToSocket("create")}
        disabled={!canCreateGame}>Create Game</button
      >
    </div>
  </form>
</div>
<span class="text-red-600 italic text-sm"> {errorMessage} </span>

<style>
  form {
    display: flex;
    align-items: center;
    flex-direction: column;
    gap: 8px;
  }

  .buttons {
    display: flex;
    flex-direction: row;
    gap: 24px;
  }

  input {
    @apply w-full rounded-xl border-2 border-solid border-slate-300 px-3 py-1;
  }

  input:focus {
    @apply rounded-2xl;
  }

  button {
    @apply rounded-3xl border-2 border-solid border-slate-300 bg-slate-100 px-4 py-1;
  }

  button:disabled {
    @apply border-slate-200 bg-slate-50 text-slate-300;
  }

  button:hover:enabled {
    @apply scale-105 bg-slate-200 shadow;
  }
</style>
