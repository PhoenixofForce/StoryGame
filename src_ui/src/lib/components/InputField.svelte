<script lang="ts">
  export let fullStory = "";
  export const MIN_SENTENCE_LENGTH = 10;

  let textArea: HTMLDivElement;

  interface stat {
    pos: number;
    done: boolean;
  }

  function highlight_text() {
    if (fullStory[fullStory.length - 1] != "\n") fullStory += "\n";

    //save cursor position
    let selection = window.getSelection();
    if (selection === null) return;

    let node = selection.focusNode;
    if (node === null) return;

    let offset = selection.focusOffset;
    let pos = getCursorPosition(textArea, node, offset, {
      pos: 0,
      done: false,
    });
    if (offset === 0) pos.pos += 0.5;

    //highlight text
    let parts = splitStoryIntoStartAndEnd(fullStory);
    textArea.innerHTML =
      sanitize(parts[0]) + "<b>" + sanitize(parts[1]) + "</b>";

    //apply cursor position
    selection.removeAllRanges();
    let range = setCursorPosition(textArea, document.createRange(), {
      pos: pos.pos,
      done: false,
    });
    range.collapse(true);
    selection.addRange(range);
  }

  export function splitStoryIntoStartAndEnd(input: string): string[] {
    let sentences = input.split(/[.?!]/);
    let sentence_to_show = sentences.length - 1;
    while (
      sentence_to_show >= 1 &&
      sentences[sentence_to_show].trim().length < MIN_SENTENCE_LENGTH
    ) {
      sentence_to_show -= 1;
    }

    let snippetStart = 0;
    for (let j = 0; j < sentence_to_show; j++) {
      snippetStart += sentences[j].length + 1;
    }

    if (
      input.includes("~") &&
      input.substring(input.lastIndexOf("~")).length > MIN_SENTENCE_LENGTH
    ) {
      snippetStart = input.lastIndexOf("~");
    }

    return [
      input.substring(0, snippetStart),
      input.substring(snippetStart, input.length),
    ];
  }

  //copied from https://stackoverflow.com/a/70800591
  function getCursorPosition(
    parent: ChildNode,
    node: Node,
    offset: number,
    stat: stat,
  ) {
    if (stat.done) return stat;

    let currentNode = null;
    if (parent.childNodes.length == 0) {
      stat.pos += parent.textContent!.length;
      return stat;
    }

    for (let i = 0; i < parent.childNodes.length && !stat.done; i++) {
      currentNode = parent.childNodes[i];
      if (currentNode === node) {
        stat.pos += offset;
        stat.done = true;
        return stat;
      }

      getCursorPosition(currentNode, node, offset, stat);
    }

    return stat;
  }

  //copied from https://stackoverflow.com/a/70800591
  function setCursorPosition(
    parent: ChildNode,
    range: Range,
    stat: stat,
  ): Range {
    if (stat.done) return range;

    if (parent.childNodes.length > 0) {
      let currentNode = null;
      for (let i = 0; i < parent.childNodes.length && !stat.done; i++) {
        currentNode = parent.childNodes[i];
        setCursorPosition(currentNode, range, stat);
      }
      return range;
    }

    const textLength = parent.textContent!.length;
    if (textLength >= stat.pos) {
      range.setStart(parent, stat.pos);
      stat.done = true;
    } else {
      stat.pos = stat.pos - textLength;
    }

    return range;
  }

  //copied from https://stackoverflow.com/a/48226843
  function sanitize(string: string): string {
    const map = {
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#x27;",
      "/": "&#x2F;",
    };
    const reg = /[&<>"'/]/gi;
    return string;
  }

  //Overwrite onPaste to keep new lines
  function onPaste(e: ClipboardEvent) {
    e.preventDefault();
    let clipboard = e.clipboardData!.getData("text/plain");
    document.execCommand("insertHTML", false, clipboard);
  }

  export function resetStory() {
    fullStory = "";
  }
</script>

<div
  contenteditable="true"
  bind:textContent={fullStory}
  bind:this={textArea}
  placeholder="Type your message"
  class="textarea w-full h-64"
  on:input={highlight_text}
  on:paste={onPaste}
/>

<style>
</style>
