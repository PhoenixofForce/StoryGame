<script>
  import { connect } from "../services/gameservice";

  let username = "";
  let roomCode = "";

  $: canJoinGame = username.length > 0 && roomCode.length > 0;
</script>

<div>
  <div class="text-5xl mb-4 font-bold tracking-wide drop-shadow">
    The Story Game
  </div>
  <p class="mb-60 italic text-slate-400">
    ~ This site is currently under construction ~
  </p>

  <form on:submit|preventDefault={() => connect(username, roomCode)}>
    <input bind:value={username} placeholder="Username" />
    <input bind:value={roomCode} placeholder="Room Code" />
    <div class="buttons">
      <button disabled={!canJoinGame}>Join Game</button>
      <button disabled={!canJoinGame}>Create Game</button>
    </div>
  </form>
</div>

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
