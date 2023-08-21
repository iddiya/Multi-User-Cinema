window.checkCategories = function (thisSelect, name) {
    console.log("Name: " + name);
    let allSelects = document.getElementsByName(name);
    let allSelectsStr = JSON.stringify(allSelects);
    console.log("All selects: " + allSelectsStr);
    let otherSelects = [];
    for (let i = 0; i < allSelects.length; i++) {
        if (allSelects[i].id !== thisSelect.id) {
            otherSelects.push(allSelects[i]);
        }
    }
    let otherSelectsStr = JSON.stringify(otherSelects);
    console.log("Other selects: " + otherSelectsStr);
    for (let i = 0; i < otherSelects.length; i++) {
        let otherSelect = otherSelects[i];
        for (let j = 0; j < otherSelect.options.length; j++) {
            if (otherSelect.options[i].value === thisSelect.value) {
                otherSelect.options[i].setAttribute('hidden', 'hidden');
            } else {
                otherSelect.options[i].removeAttribute('hidden');
            }
        }
    }
}