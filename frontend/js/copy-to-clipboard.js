window.copyToClipboard = (str) => {
    const textarea = document.createElement("textarea");
    textarea.value = str;
    textarea.style.position = "absolute";
    textarea.style.opacity = "0";
    document.body.appendChild(textarea);

    if (navigator.userAgent.match(/ipad|ipod|iphone/i)) {
        alert("iphone!")
        textarea.contentEditable = 'true';
        textarea.readOnly = false;
        let range = document.createRange();
        range.selectNodeContents(textarea);
        let sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
        textarea.setSelectionRange(0, 999999);
        alert("iphone done!")
    } else {
        textarea.select();
    }

    document.execCommand("copy");
    document.body.removeChild(textarea);
};
