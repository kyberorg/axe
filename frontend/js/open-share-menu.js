window.openShareMenu = (link) => {
    if (!window.navigator.share) {
        alert("Your browser doesn't support sharing")
        return
    }

    window.navigator.share({
        title: link,
        text: link,
        url: link,
    })
        .then(() => console.log("Item shared"))
        .catch(() => console.error("Failed to share link"));
}