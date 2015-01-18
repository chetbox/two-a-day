var DAY = 1000*60*60*24;

function daysAgo(date) {
  var today = new Date();
  return Math.floor((today - date) / DAY);
}

function shortDayStr(date) {
  return ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][date.getDay()];
}

function dateStr(date) {
  return date.getUTCFullYear() + '-' +
         new String("00" + (date.getUTCMonth() + 1)).slice(-2) + '-' +
         new String("00" + date.getUTCDate()).slice(-2);
}
