<script lang="ts">
  import { Sun, Moon, Volume2, VolumeOff } from "lucide-svelte";
  import { options } from "$lib/services/options";
  import { cancel } from "$lib/services/speakService";
  import { setLocale } from "$paraglide/runtime.js";

  function toggleSounds() {
    $options.allowSounds = !$options.allowSounds;
    localStorage.setItem("allowSounds", $options.allowSounds + "");
    if (!$options.allowSounds) {
      cancel();
    }
  }

  let isDark = $state(localStorage.getItem("theme") === "forest");
  const saved = localStorage.getItem("theme");
  if (saved) {
    document.documentElement.setAttribute("data-theme", saved);
  }

  function toggleDarkMode() {
    isDark = !isDark;
    const theme = isDark ? "forest" : "lemonade";
    localStorage.setItem("theme", theme);
    document.documentElement.classList.add("no-transitions");
    document.documentElement.setAttribute("data-theme", theme);
    requestAnimationFrame(() => {
      document.documentElement.classList.remove("no-transitions");
    });
  }

  function toggleLanguage() {
    let language: "en" | "de" = "en";
    if ($options.language === "en") {
      language = "de";
    }

    setLocale(language, { reload: false });
    $options.language = language;
    localStorage.setItem("language", $options.language + "");
  }

  const version = __APP_VERSION__.startsWith("v")
    ? __APP_VERSION__
    : "v" + __APP_VERSION__;
</script>

<div class="fixed bottom-4 left-4 z-50 flex flex-row gap-4">
  <label
    class="swap swap-rotate opacity-50 transition-all duration-300 will-change-transform hover:scale-110 hover:opacity-100 active:scale-90"
  >
    <input type="checkbox" checked={isDark} onchange={toggleDarkMode} />

    <Sun class="swap-off" size="24" />
    <Moon class="swap-on" size="24" />
  </label>

  <label
    class="swap swap-rotate opacity-50 transition-all duration-300 will-change-transform hover:scale-110 hover:opacity-100 active:scale-90"
  >
    <input
      type="checkbox"
      checked={$options.allowSounds}
      onchange={toggleSounds}
    />

    <VolumeOff class="swap-off" size="24" />
    <Volume2 class="swap-on" size="24" />
  </label>

  <label
    class="swap swap-rotate opacity-50 transition-all duration-300 will-change-transform hover:scale-110 hover:opacity-100 active:scale-90"
  >
    <input
      type="checkbox"
      checked={$options.language === "en"}
      onchange={toggleLanguage}
    />

    <span class="swap-off">DE</span>
    <span class="swap-on">EN</span>
  </label>
</div>

<a
  href="https://github.com/PhoenixofForce/StoryGame/releases/tag/{version}"
  target="_"
  class="text-base-content/30 fixed right-8 bottom-4 text-xs select-none"
>
  {version}
</a>
