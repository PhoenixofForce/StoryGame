import { writable, type Writable } from "svelte/store";

export const debugStoreRegistry = writable<
  { label: string; store: Writable<unknown> }[]
>([]);

export function registerStore(
  label: string,
  store: Writable<unknown>,
): () => void {
  if (!import.meta.env.DEV) return () => {};
  debugStoreRegistry.update((s) => [...s, { label, store }]);
  return () => removeStore(label);
}

function removeStore(label: string) {
  if (!import.meta.env.DEV) return;
  debugStoreRegistry.update((s) => s.filter((e) => e.label !== label));
}
