<script>
  import { SunMedium, Moon, Volume2, VolumeOff } from "lucide-svelte";
  import { options } from "../services/options";
  import { cancel } from "../services/speakService";

  function toggleSounds() {
    $options.allowSounds = !$options.allowSounds;
    localStorage.setItem("allowSounds", $options.allowSounds + "");
    if (!$options.allowSounds) {
      cancel();
    }
  }

  function toggleDarkMode() {
    $options.darkMode = !$options.darkMode;
    localStorage.setItem("darkMode", $options.darkMode + "");
    document.documentElement.classList.toggle("dark");
  }
</script>

<div class="fixed bottom-4 left-8">
  <button
    class="plain mr-2 text-slate-400 hover:scale-105 hover:text-slate-800 dark:hover:text-slate-100"
    on:click={toggleDarkMode}
  >
    {#if $options.darkMode}
      <Moon />
    {:else}
      <SunMedium />
    {/if}
  </button>

  <button
    class="plain text-slate-400 hover:scale-105 hover:text-slate-800 dark:hover:text-slate-100"
    on:click={toggleSounds}
  >
    {#if $options.allowSounds}
      <Volume2 />
    {:else}
      <VolumeOff />
    {/if}
  </button>
</div>

<div class="fixed right-8 bottom-4 text-xs text-slate-400">
  v{__APP_VERSION__}
</div>
