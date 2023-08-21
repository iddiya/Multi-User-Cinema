let q = 1, qMax = 0;

$(function () {
    let form = $('#cascading-form div.group');
    qMax = form.length;
    form.hide();
    $('#cascading-form div.group:nth-child(1)').show();
    $('#btnNext').on('click', function (event) {
        event.preventDefault();
        handleClick();
    });
});

function handleClick() {
    if (q < qMax) {
        $('#cascading-form div.group:nth-child(' + q + ')').hide();
        $('#cascading-form div.group:nth-child(' + (q + 1) + ')').show();
        if (q === (qMax - 1)) {
            $('#btnNext').html('Submit');
        }
        q++;
    } else {
        alert('Submitting');
    }
}