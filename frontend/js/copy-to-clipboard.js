window.copyToClipboard = (str) => {
    //using new Clipboard API
    if (navigator && navigator.clipboard && navigator.clipboard.writeText) {
        requestPermissions().then(r => copyUsingClipboardAPI(str)); //TODO fallback
    } else {
        //fallback to deprecated stuff
        alert("Fallback activated");
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

async function requestPermissions() {
    const queryOpts = {name: 'clipboard-write', allowWithoutGesture: false};
    const permissionStatus = await navigator.permissions.query(queryOpts);
// Примет значение 'granted', 'denied' или 'prompt':
    alert(permissionStatus.state);
}

function copyUsingClipboardAPI(str) {
    navigator.clipboard.writeText(str).then(
        () => alert("Copied text OK. Text=" + str),
        (e) => alert("Failed to copy text. E=" + e)
    );
}