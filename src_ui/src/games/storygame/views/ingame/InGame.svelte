<script lang="ts">
  import { Send } from "lucide-svelte";
  import InputField from "../../components/InputField.svelte";
  import { sendSubmitStoryMessage } from "../../services/storyGameService";
  import { lobbyStore } from "$common/views/lobby/LobbyStore";
  import { inGameStore } from "./InGameStore";
  import Spinner from "$common/components/Spinner.svelte";
  import Button from "$common/components/Button.svelte";
  import PageLayout from "$common/views/PageLayout.svelte";
  import { m } from "$paraglide/messages.js";

  let fullStory = "";
  let storyInputField: InputField;

  function sendStory() {
    const teaser = storyInputField.splitStoryIntoStartAndEnd(fullStory)[1];
    sendSubmitStoryMessage(fullStory.replace("~", ""), teaser.replace("~", ""));
    storyInputField.resetStory();
    inGameStore.update((s) => ({ ...s, submittedStory: true }));
  }
</script>

<PageLayout noCard={true}>
  <svelte:fragment slot="content">
    {#if !$inGameStore.hasPlayerSubmitted}
      <div class="flex h-full flex-col">
        <div class="mb-2">
          <div
            class="text-left text-2xl font-bold tracking-wide drop-shadow-sm sm:text-3xl"
          >
            {m.storygame_round({
              currentRound: $inGameStore.currentRound,
              maxRounds: $inGameStore.maxRounds,
            })}
          </div>
          <p class="text-base-content/70 min-h-5 text-left text-sm italic">
            {#if $inGameStore.finishedPlayers > 0}
              {m.storygame_players_ready({
                playersReady: $inGameStore.finishedPlayers,
                maxPlayers: $lobbyStore.players.length,
              })}
            {/if}
          </p>
        </div>
        <p class="mb-2 text-left">{$inGameStore.lastStorySnippet}</p>
        <div class="min-h-0 flex-1">
          <InputField bind:this={storyInputField} bind:fullStory />
        </div>
      </div>
    {:else}
      <div class="flex h-full w-full flex-col items-center justify-center">
        <Spinner />
        <div class="text-base-content/70 mt-8 tracking-widest">
          {m.storygame_waiting_other_players()}
        </div>
      </div>
    {/if}
  </svelte:fragment>

  <svelte:fragment slot="actions">
    {#if !$inGameStore.hasPlayerSubmitted}
      <div class="text-base-content/70 mr-auto text-sm italic md:text-base">
        <b>{m.common_hint()}</b>
        {m.storygame_hint()}
      </div>
      <Button
        data-testid="StoryGame.sendStory"
        type="primary"
        class="w-full sm:w-48"
        icon={Send}
        onclick={sendStory}
        disabled={!fullStory ||
          fullStory.length < storyInputField.MIN_SENTENCE_LENGTH}
      >
        {m.common_send()}
      </Button>
    {/if}
  </svelte:fragment>
</PageLayout>
