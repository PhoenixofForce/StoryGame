import { writable } from "svelte/store";

export const options = writable({
  allowSounds: getFromLocalStorage("allowSounds", true),
  darkMode: getFromLocalStorage(
    "darkMode",
    globalThis.matchMedia("(prefers-color-scheme: dark)").matches,
  ),
});

if (
  getFromLocalStorage(
    "darkMode",
    globalThis.matchMedia("(prefers-color-scheme: dark)").matches,
  )
) {
  document.documentElement.classList.toggle("dark");
}

function getFromLocalStorage(itemKey: string, defaultValue: boolean): boolean {
  const item = localStorage.getItem(itemKey);
  if (item === null) return defaultValue;

  return "true" === item;
}
