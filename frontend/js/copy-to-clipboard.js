window.copyToClipboard = (str) => {
    const textarea = document.createElement("textarea");
    textarea.value = str;
    textarea.style.position = "absolute";
    textarea.style.opacity = "0";
    document.body.appendChild(textarea);

    if(isOS()) {
        let range = document.createRange()
        range.selectNodeContents(textarea)
        let selection = window.getSelection()
        selection.removeAllRanges()
        selection.addRange(range);
        textarea.setSelectionRange(0, 999999);
    } else {
        input.select()
    }

    document.execCommand("copy")
    document.body.removeChild(textarea);
};

function isOS() {
    return navigator.userAgent.match(/ipad|iphone/i)
}
