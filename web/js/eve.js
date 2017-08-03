/*
 Common JS functions to EVE
 */

function formatDate(date) {
    return pad(date.getHours()) + ":" + pad(date.getMinutes()) + ":" + pad(date.getSeconds())
}

function pad(number) {
    return (number < 10) ? '0' + number : number;
}