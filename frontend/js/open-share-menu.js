window.openShareMenu = (content) => {
    window.navigator.share(content)
        .then(() => console.log("Item shared"))
        .catch(() => console.error("Failed to share link"));
}