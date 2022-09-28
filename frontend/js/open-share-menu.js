function openShareMenu(content) {
    navigator.share(content).then(() => console.log("Item shared"));
}