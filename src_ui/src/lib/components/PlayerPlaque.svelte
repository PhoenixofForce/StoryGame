<script lang="ts">
  export let name!: string;
  export let namePosition: "left" | "below" | "right" | "top" | "hidden" =
    "hidden";
</script>

<div
  class="flex items-center mb-1"
  class:flex-col={namePosition === "below"}
  class:flex-col-reverse={namePosition === "top"}
  class:flex-row={namePosition === "right"}
  class:flex-row-reverse={namePosition === "left"}
>
  <div
    class="bg-slate-200 w-9 h-9 p-2 rounded-full flex items-center justify-center
    border-slate-400 shadow border-2 font-bold text-lg mx-1 select-none"
  >
    <span>{name[0]}</span>
  </div>
  {#if namePosition !== "hidden"}
    <span class="font-medium">
      {name}
    </span>
  {/if}
</div>

<stlye> </stlye>
