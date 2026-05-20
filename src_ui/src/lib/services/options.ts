import { writable } from "svelte/store";

export const options = writable({
  allowSounds: getFromLocalStorage("allowSounds", true),
  language: getStringFromLocalStorage("language", "en"),
});

function getFromLocalStorage(itemKey: string, defaultValue: boolean): boolean {
  const item = localStorage.getItem(itemKey);
  if (item === null) return defaultValue;

  return "true" === item;
}

function getStringFromLocalStorage(
  itemKey: string,
  defaultValue: string,
): string {
  const item = localStorage.getItem(itemKey);
  if (item === null) return defaultValue;
  return item;
}
