<script>
  export let noCard = false;
  export let showSidebar = false;
</script>

<div
  class="mx-auto mt-12 flex w-full max-w-4xl flex-col items-center px-4 sm:mt-16 sm:px-8 xl:mt-20"
>
  {#if showSidebar}
    <div class="mb-4 w-full md:fixed md:top-5 md:left-12 md:mb-0 md:w-auto">
      <slot name="sidebar" />
    </div>
  {/if}

  <div class="mb-4 flex min-h-12 w-full items-end justify-center">
    <slot name="title" />
  </div>

  <div class="w-full">
    {#if noCard}
      <div class="h-128 w-full overflow-hidden xl:h-160">
        <slot name="content" />
      </div>
    {:else}
      <div class="w-full rounded-2xl bg-slate-50 shadow-lg dark:bg-slate-700">
        <div
          class="h-128 overflow-y-auto px-8 py-8 sm:px-12 xl:h-160 xl:px-16 xl:py-12"
        >
          <slot name="content" />
        </div>
      </div>
    {/if}

    <div
      class="mt-3 flex w-full flex-col-reverse items-center justify-end gap-3 sm:flex-row"
    >
      <slot name="actions" />
    </div>
  </div>
</div>
