import { render, screen, fireEvent } from "@testing-library/svelte";
import { expect, test, beforeEach } from "vitest";

import InGame from "./InGame.svelte";
import { inGameStore } from "./InGameStore";
import { StoryGamePhase } from "../../../../common/services/messageTypes";
import { tick } from "svelte";

const defaultState = () => ({
  type: "story_game_update" as const,
  message: "",
  error: false,
  phase: StoryGamePhase.WRITING,
  currentRound: 1,
  maxRounds: 2,
  finishedPlayers: 0,
  lastStorySnippet: "",
  hasPlayerSubmitted: false,
  allChaptersRevealed: false,
  allStoriesRevealed: false,
  currentAuthor: "",
  revealedChapters: [],
});

beforeEach(() => {
  inGameStore.set(defaultState());
});

test("should hide inputfield after user submits", async () => {
  render(InGame, { props: {}, context: new Map() });

  expect(screen.getByTestId("StoryGame.Input")).toBeVisible();

  inGameStore.update((s) => ({ ...s, hasPlayerSubmitted: true }));
  await tick();

  expect(screen.queryByTestId("StoryGame.Input")).not.toBeInTheDocument();
  expect(screen.queryByTestId("StoryGame.sendStory")).not.toBeInTheDocument();
});

test("players should keep input when other finish", async () => {
  render(InGame, { props: {}, context: new Map() });

  const text = "Some text";
  const input = screen.getByTestId("StoryGame.Input");
  await fireEvent.input(input, { target: { textContent: text } });

  expect(screen.getByTestId("StoryGame.Input")).toHaveTextContent(text);

  inGameStore.update((s) => ({ ...s, finishedPlayers: 1 }));
  await tick();

  expect(screen.getByTestId("StoryGame.Input")).toHaveTextContent(text);
});

test("send should be disabled for short texts", async () => {
  render(InGame, { props: {}, context: new Map() });

  expect(screen.getByTestId("StoryGame.sendStory")).toBeVisible();
  expect(screen.getByTestId("StoryGame.sendStory")).toBeDisabled();

  const text = "Some";
  const input = screen.getByTestId("StoryGame.Input");
  await fireEvent.input(input, { target: { textContent: text } });

  expect(screen.getByTestId("StoryGame.sendStory")).toBeVisible();
  expect(screen.getByTestId("StoryGame.sendStory")).toBeDisabled();
});

test("send should be active for longer texts", async () => {
  render(InGame, { props: {}, context: new Map() });

  expect(screen.getByTestId("StoryGame.sendStory")).toBeVisible();
  expect(screen.getByTestId("StoryGame.sendStory")).toBeDisabled();

  const text = "Some longer text";
  const input = screen.getByTestId("StoryGame.Input");
  await fireEvent.input(input, { target: { textContent: text } });

  expect(screen.getByTestId("StoryGame.sendStory")).toBeVisible();
  expect(screen.getByTestId("StoryGame.sendStory")).not.toBeDisabled();
});
