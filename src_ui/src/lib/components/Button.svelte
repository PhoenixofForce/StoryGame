<script lang="ts">
    import type { LocalizedString } from "$paraglide/runtime";
  import type { Icon } from "lucide-svelte";
  import type { ComponentType } from "svelte";
  import type { MouseEventHandler } from "svelte/elements";

  export let icon: ComponentType<Icon> | undefined = undefined;
  export let classes = "";
  export let disabled = false;
  export let onClick: MouseEventHandler<HTMLButtonElement> | undefined =
    undefined;
  export let type: "primary" | "default" = "default";
  export let tooltip: LocalizedString | string = "";

  const typeToClassesMap = {
    primary: "btn-primary",
    default: "btn-neutral",
  };

  const typeToTooltipClassesMap = {
      primary: "tooltip-primary",
      default: "tooltip-neutral",
    };
</script>

<div class:tooltip={tooltip} class="tooltip-right {typeToTooltipClassesMap[type]}" data-tip="{tooltip}">
    <button
      class="btn whitespace-nowrap {classes} {typeToClassesMap[type]}"
      on:click={onClick}
      {disabled}
    >
      {#if icon}
        <svelte:component this={icon} class="mr-1" />
      {/if}
      <slot></slot>
    </button>
</div>
