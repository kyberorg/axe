window.openShareMenu = (link, description) => {
    if (!window.navigator.share) {
        alert("Your browser doesn't support sharing")
        return
    }
    let shareData;
    if (description != null && description) {
        shareData = {
            title: link,
            text: description,
            url: link,
        }
    } else {
        shareData = {
            title: link,
            url: link,
        }
    }

    window.navigator.share(shareData)
        .then(() => console.log("Item shared"))
        .catch(() => console.error("Failed to share link"));
}