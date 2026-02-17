<script lang="ts">
  export let name!: string;
  export let namePosition: "left" | "below" | "right" | "top" | "hidden" =
    "right";

  export let color: string = "slate";
</script>

<div
  class="mb-1 flex items-center"
  class:flex-col={namePosition === "below"}
  class:flex-col-reverse={namePosition === "top"}
  class:flex-row={namePosition === "right"}
  class:flex-row-reverse={namePosition === "left"}
>
  <div
    class=" mx-1 flex h-9 w-9 items-center justify-center rounded-full
     border-2 p-2 text-lg font-bold shadow select-none
     {color === 'slate'
      ? 'border-slate-400 bg-slate-200 text-slate-950 dark:bg-slate-700 dark:text-slate-400'
      : ''}
     {color === 'red'
      ? 'border-red-400 bg-red-200 text-red-950 dark:bg-red-700 dark:text-red-400'
      : ''}
     {color === 'green'
      ? 'border-green-400 bg-green-200 text-green-950 dark:bg-green-700 dark:text-green-400'
      : ''}
     {color === 'blue'
      ? 'border-blue-400 bg-blue-200 text-blue-950 dark:bg-blue-700 dark:text-blue-400'
      : ''}
     {color === 'yellow'
      ? ' border-yellow-400 bg-yellow-200 text-yellow-950 dark:bg-yellow-700 dark:text-yellow-400'
      : ''}"
  >
    <span>{name[0]}</span>
  </div>
  {#if namePosition !== "hidden"}
    <span class="font-medium text-{color}-950 dark:text-{color}-400">
      {name}
    </span>
  {/if}
</div>

<stlye> </stlye>
