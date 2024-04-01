<script lang="ts">
  import {
    addEventHandler,
    removeEventHandler,
  } from "../services/websocketService";
  import { sendSubmitStoryMessage } from "../services/gameService";
  import { lobbyStore } from "../services/lobbyService";
  import type {
    GameStateUpdateMessage,
    StartRoundTriggerMessage,
  } from "../services/messageTypes";
  import { displayEvaluation } from "../services/navigationService";
  import { onDestroy } from "svelte";

  let storyEnd = "";
  let story = "";
  let textArea;

  let currentRound = 0;
  let maxRounds = 0;
  let submittedStory = false;

  let playersReady = 0;

  let handlers = [
    addEventHandler("start_round", {
      onSuccess: (e) => {
        const data = e as StartRoundTriggerMessage;
        storyEnd = data.lastStorySnippet;
        currentRound = data.currentRound;
        maxRounds = data.maxRounds;

        story = "";
        submittedStory = false;
        playersReady = 0;
      },
    }),

    addEventHandler("game-update", {
      onSuccess: (e) => {
        const data = e as GameStateUpdateMessage;
        playersReady = data.finishedPlayers;
      },
    }),

    addEventHandler("end_game", {
      onSuccess: displayEvaluation,
    }),
  ];

  //THIS FUNCTION HAS TO BE KEPT SYNCHRONIZED TO Story.prepareStoryText in the backend
  function highlightNextStoryText(input) {
      let MIN_SENTENCE_LENGTH = 10;

      let sentences = input.split(/[.?!]/);
      let sentence_to_show = sentences.length - 1;
      while (sentence_to_show >= 1 && sentences[sentence_to_show].trim().length < MIN_SENTENCE_LENGTH) {
        sentence_to_show -= 1;
      }

      let snippetStart = 0;
      for (let j = 0; j < sentence_to_show; j++) {
        snippetStart += sentences[j].length + 1;
      }

      return [input.substring(0, snippetStart),input.substring(snippetStart, input.length)];
  }

  //copied from https://stackoverflow.com/a/70800591
  function getCursorPosition(parent, node, offset, stat) {
    if (stat.done) return stat;
    let currentNode = null;
    if (parent.childNodes.length == 0) {
      stat.pos += parent.textContent.length;
    } else {
      for (let i = 0; i < parent.childNodes.length && !stat.done; i++) {
        currentNode = parent.childNodes[i];
        if (currentNode === node) {
          stat.pos += offset;
          stat.done = true;
          return stat;
        } else getCursorPosition(currentNode, node, offset, stat);
      }
    }
    return stat;
  }

  //copied from https://stackoverflow.com/a/70800591
  function setCursorPosition(parent, range, stat) {
    if (stat.done) return range;

    let currentNode = null;
    if (parent.childNodes.length == 0) {
      if (parent.textContent.length >= stat.pos) {
        range.setStart(parent, stat.pos);
        stat.done = true;
      } else {
        stat.pos = stat.pos - parent.textContent.length;
      }
    } else {
      for (let i = 0; i < parent.childNodes.length && !stat.done; i++) {
        currentNode = parent.childNodes[i];
        setCursorPosition(currentNode, range, stat);
      }
    }
    return range;
  }

  //copied from https://stackoverflow.com/a/48226843
  function sanitize(string) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#x27;',
        "/": '&#x2F;',
    };
    const reg = /[&<>"'/]/ig;
    return string.replace(reg, (match)=>(map[match]));
  }

  function highlight_text() {
    if (story[story.length-1] != "\n") story += "\n";

    //save cursor position
    let sel = window.getSelection();
    let node = sel.focusNode;
    let offset = sel.focusOffset;
    let pos = getCursorPosition(textArea, node, offset, { pos: 0, done: false });
    if (offset === 0) pos.pos += 0.5;

    //highlight text
    let parts = highlightNextStoryText(story);
    textArea.innerHTML = sanitize(parts[0]) + "<b>" + sanitize(parts[1]) + "</b>";

    //apply cursor position
    sel.removeAllRanges();
    let range = setCursorPosition(textArea, document.createRange(), {
      pos: pos.pos,
      done: false,
    });
    range.collapse(true);
    sel.addRange(range);
  }

  //Overwrite onPaste to keep new lines
  function onPaste(e) {
      e.preventDefault();
      let clipboard = (e.originalEvent || e).clipboardData.getData('text/plain');
      console.log(clipboard);
      document.execCommand("insertHTML", false, clipboard);
  }

  function sendStory() {
    sendSubmitStoryMessage(story);
    story = "";
    submittedStory = true;
  }

  onDestroy(() => {
    for (let handler of handlers) {
      removeEventHandler(handler);
    }
  });
</script>

<div class="absolute top-4 left-8">
  <div class="text-5xl mb-4 font-bold tracking-wide drop-shadow">
    The Story Game
  </div>
  <p class="italic text-slate-400">
    Round {currentRound} / {maxRounds}<br />
    {#if playersReady > 0}
      <span
        >{playersReady} / {$lobbyStore.players.length} Players are ready</span
      >
    {/if}
  </p>
</div>

<div class="mt-32 md:mt-64">
    {#if !submittedStory}
      <p style="text-align: left">{storyEnd}</p>
      <div contenteditable="true"
        bind:textContent={story}
        bind:this={textArea}
        placeholder="Type your message"
        class="textarea mb-4 w-full h-64"
        on:input={highlight_text}
        on:paste={onPaste}
      />
      <button
        class="blue float-right w-full md:w-32"
        disabled={!story}
        on:click={sendStory}
      >
        Send
      </button>
    {:else}
      <div class="tracking-widest text-center">Waiting for other players...</div>
    {/if}
  </div>

<style>
</style>
