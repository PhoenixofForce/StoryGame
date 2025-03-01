import { writable } from "svelte/store";

export const options = writable({
  allowSounds: getFromLocalStorage("allowSounds", true),
  darkMode:
    getFromLocalStorage("darkMode", false) ||
    window.matchMedia("(prefers-color-scheme: dark)").matches,
});

if (
  getFromLocalStorage("darkMode", false) ||
  window.matchMedia("(prefers-color-scheme: dark)").matches
) {
  document.documentElement.classList.toggle("dark");
}

function getFromLocalStorage(itemKey: string, defaultValue: boolean): boolean {
  const item = localStorage.getItem(itemKey);
  if (!itemKey) return defaultValue;
  return "true" === item;
}
