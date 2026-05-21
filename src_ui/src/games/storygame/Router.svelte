<script lang="ts">
  import { sendMessage } from "$common/services/websocketService";
  import { onMount } from "svelte";
  import Evaluation from "./views/evaluation/Evaluation.svelte";
  import InGame from "./views/ingame/InGame.svelte";
  import { inGameStore } from "./views/ingame/InGameStore";

  onMount(() => {
    sendMessage({ type: "request_state", message: "", error: false });
  });
</script>

{JSON.stringify($inGameStore)}
{#if $inGameStore.phase === "REVEALING"}
  <Evaluation />
{:else if $inGameStore.phase === "WRITING"}
  <InGame />
{/if}
