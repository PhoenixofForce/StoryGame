<script lang="ts">
  let names = ["Alice", "Bob", "Charlie", "David"];
  let storyEnd =
    "Dictum sit amet justo donec enim diam vulputate ut pharetra. Ultrices neque ornare aenean euismod elementum nisi quis. At tempor commodo ullamcorper a. Sagittis orci a scelerisque purus.";
  let story = "";

  function sendStory(story: String) {
    console.log("Message sent:", story);
  }
</script>

<div class="text-5xl mb-4 font-bold tracking-wide drop-shadow">
  The Story Game
</div>
<p class="mb-60 italic text-slate-400">Round 3 / 8</p>

<div class="flex">
  <div class="left-column">
    <ul>
      {#each names as name}
        <li>{name}</li>
      {/each}
    </ul>
  </div>

  <div class="right-column">
    <p style="text-align: left">{storyEnd}</p>
    <form on:submit|preventDefault={() => sendStory(story)}>
      <input bind:value={story} placeholder="Type your message" />
    </form>
    <div class="buttons">
      <button disabled={!story}>Send</button>
      <button disabled={story.length > 0}>Edit</button>
    </div>
  </div>
</div>

<style>
  .buttons {
    display: flex;
    flex-direction: row;
    gap: 24px;
  }

  .flex {
    display: flex;
    gap: 20px;
  }

  .left-column {
    flex: 1;
  }

  .right-column {
    flex: 3;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    margin-bottom: 8px;
    padding: 4px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }

  input,
  button {
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
