<script>
  export let noCard = false;
</script>

<div
  class="w-full h-full flex flex-col sm:flex-row gap-4"
>
  <div class="flex-1 px-8 py-6">
    <slot name="sidebar" />
  </div>

  <div class="flex-5 flex flex-col gap-4 px-8 sm:px-0 max-w-4xl">
      <div class="flex-1 flex flex-col justify-end items-center">
        <slot name="title" />
      </div>
    <div  class="{noCard ? "" : "card bg-neutral px-8 py-6"} flex-3">
      <slot name="content" />
    </div>
    <div class="flex-1 flex flex-col sm:flex-row justify-end gap-4">
      <slot name="actions" />
    </div>
  </div>

  <div class="flex-1 px-8 py-6">
      <slot name="rightbar" />
  </div>
</div>
