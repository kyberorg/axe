function waitForElm(selector) {
    return new Promise(resolve => {
        if (document.querySelector(selector)) {
            return resolve(document.querySelector(selector));
        }

        const observer = new MutationObserver(mutations => {
            if (document.querySelector(selector)) {
                resolve(document.querySelector(selector));
                observer.disconnect();
            }
        });

        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    });
}

function showTestName(testName) {
    waitForElm("#testName").then((elm) => {
        elm.textContent = "TestName: " + testName;
    });
}

function removeTestName() {
    waitForElm("#testName").then((elm) => {
        elm.textContent = '';
    });
}
