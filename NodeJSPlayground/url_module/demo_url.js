var adr = 'http://localhost:8080/default.htm?year=2017&month=february';
var targetUrl = new URL(adr);

console.log(targetUrl.host);
console.log(targetUrl.pathname);
console.log(targetUrl.search);

var params = targetUrl.searchParams
console.log(params.get('year') + " " + params.get('month'))