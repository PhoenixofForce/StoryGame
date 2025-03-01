<script lang="ts">
  import type { Icon } from "lucide-svelte";
  import type { ComponentType } from "svelte";

  export let icon: ComponentType<Icon> | undefined = undefined;
  export let classes = "";
  export let disabled = false;
  export let onClick: any | undefined = undefined;
  export let type: "primary" | "default" = "default";

  const typeToClassesMap = {
    primary:
      "bg-gradient-to-tr from-lime-100 to-orange-100 dark:from-green-800 dark:to-sky-800",
    default: "",
  };
</script>

<button
  class="whitespace-nowrap {classes} {typeToClassesMap[type]}"
  on:click={onClick}
  {disabled}
>
  {#if icon}
    <svelte:component this={icon} class="mr-1" strokeWidth={1} />
  {/if}
  <slot></slot>
</button>
