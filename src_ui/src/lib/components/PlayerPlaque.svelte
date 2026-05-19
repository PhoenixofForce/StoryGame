<script lang="ts">
  export let name!: string;
  export let tags: string[];
</script>

<div
  class="bg-base-300 text-base-content card mb-1 flex w-fit flex-row items-center px-4 py-2"
>
  <div
    class="bg-primary text-primary-content mr-2 flex h-10 w-10 items-center justify-center rounded-full text-lg font-bold uppercase select-none"
  >
    <span>{name[0]}</span>
  </div>

  <div class="flex flex-col">
    <span class="font-medium">
      {name}
    </span>
    <span>
      {#each tags as tag (tag)}
        <div class="badge badge-accent badge-sm mr-2 last:mr-0">{tag}</div>
      {/each}
    </span>
  </div>
</div>

<stlye> </stlye>
