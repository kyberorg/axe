window.copyToClipboard = (str) => {
    //using new Clipboard API
    if (navigator && navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(str).then(
            () => console.log("Copied text OK"),
            (e) => console.log("Failed to copy text",e)
        );
    } else {
        //fallback to deprecated stuff
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
    }
};

function isOS() {
    return navigator.userAgent.match(/ipad|iphone/i)
}
