<script>
  export let noCard = false;
</script>

<div class="flex h-full w-full flex-col gap-4 sm:flex-row">
  <div class="flex-1 px-8 py-6">
    <slot name="sidebar" />
  </div>

  <div class="flex max-w-4xl flex-5 flex-col gap-4 px-8 sm:px-0">
    <div class="flex flex-1 flex-col items-center justify-end">
      <slot name="title" />
    </div>
    <div class="{noCard ? '' : 'card bg-neutral px-8 py-6'} flex-3">
      <slot name="content" />
    </div>
    <div class="flex flex-1 flex-col justify-end gap-4 sm:flex-row">
      <slot name="actions" />
    </div>
  </div>

  <div class="flex-1 px-8 py-6">
    <slot name="rightbar" />
  </div>
</div>
