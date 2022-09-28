window.openShareMenu = (link, description) => {
    let titleString;
    if (description) {
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