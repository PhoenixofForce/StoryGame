<script lang="ts">
  import { debugStoreRegistry as stores } from "$common/services/debugStoreRegistry";

  let tabIndex = $state(0);
  $effect(() => {
    if (tabIndex >= $stores.length) {
      tabIndex = Math.max(0, $stores.length - 1);
    }
  });

  let unsubscribe: (() => void) | undefined;
  let selectedStore = $state("");
  $effect(() => {
    unsubscribe?.();
    unsubscribe = $stores[tabIndex].store.subscribe(
      (s) => (selectedStore = JSON.stringify(s, null, 2)),
    );
  });

  function writeCurrentStoreValue(state: string) {
    try {
      $stores[tabIndex].store.set(JSON.parse(state));
    } catch {}
  }
</script>

{#if import.meta.env.DEV}
  <details
    class="group bg-base-200 fixed top-0 left-0 z-50 overflow-auto rounded-br-lg p-2 text-xs opacity-75 focus-within:opacity-100 focus-within:shadow hover:opacity-100 hover:shadow"
  >
    <summary>Debug</summary>

    <div class="flex h-96 w-80 flex-col gap-2 pt-2">
      <div class="flex gap-2">
        {#each $stores as store, i (store.label)}
          <button
            class="btn btn-xs"
            class:btn-primary={tabIndex === i}
            onclick={() => (tabIndex = i)}
          >
            {store.label}
          </button>
        {/each}
      </div>

      <pre
        class="block group-focus-within:hidden group-hover:hidden">{selectedStore}</pre>
      <textarea
        class="hidden flex-1 resize-none px-1 group-focus-within:block group-hover:block"
        value={selectedStore}
        onchange={(e) => writeCurrentStoreValue(e.currentTarget.value)}
      ></textarea>
    </div>
  </details>
{/if}
