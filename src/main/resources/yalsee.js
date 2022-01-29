function scrollToResults() {
    document.querySelector('#overallArea')
        .scrollIntoView({behavior: 'smooth', block: 'start', inline: 'nearest'})
}