<script lang="ts">
  export let name!: string;
  export let namePosition: "left" | "below" | "right" | "top" | "hidden" =
    "right";

  export let color: string = "slate";
</script>

<div
  class="flex items-center mb-1"
  class:flex-col={namePosition === "below"}
  class:flex-col-reverse={namePosition === "top"}
  class:flex-row={namePosition === "right"}
  class:flex-row-reverse={namePosition === "left"}
>
  <div
    class=" w-9 h-9 p-2 rounded-full flex items-center justify-center
     shadow border-2 font-bold text-lg mx-1 select-none
     {color === 'slate' ? 'bg-slate-200 border-slate-400 text-slate-950' : ''}
     {color === 'red' ? 'bg-red-200 border-red-400 text-red-950' : ''}
     {color === 'green' ? 'bg-green-200 border-green-400 text-green-950' : ''}
     {color === 'blue' ? 'bg-blue-200 border-blue-400 text-blue-950' : ''}
     {color === 'yellow'
      ? 'bg-yellow-200 border-yellow-400 text-yellow-950'
      : ''}"
  >
    <span>{name[0]}</span>
  </div>
  {#if namePosition !== "hidden"}
    <span class="font-medium text-{color}-950">
      {name}
    </span>
  {/if}
</div>

<stlye> </stlye>
