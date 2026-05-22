<script lang="ts">
  import { sendMessage } from "$common/services/websocketService";
  import { onMount } from "svelte";
  import Evaluation from "./views/evaluation/Evaluation.svelte";
  import InGame from "./views/ingame/InGame.svelte";
  import { inGameStore } from "./views/ingame/InGameStore";
  import { registerStore } from "$common/services/debugStoreRegistry";

  onMount(() => {
    sendMessage({ type: "request_state", message: "", error: false });
    return registerStore("Story Game", inGameStore);
  });
</script>

{#if $inGameStore.phase === "REVEALING"}
  <Evaluation />
{:else if $inGameStore.phase === "WRITING"}
  <InGame />
{/if}
