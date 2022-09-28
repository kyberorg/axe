window.openShareMenu = (link, description) => {
    if (!window.navigator.share) {
        alert("Your browser doesn't support sharing")
        return
    }
    let titleString;
    if (description != null && description) {
        titleString = description;
    } else {
        titleString = link;
    }

    window.navigator.share({
        title: titleString,
        text: description,
        url: link,
    })
        .then(() => console.log("Item shared"))
        .catch(() => console.error("Failed to share link"));
}