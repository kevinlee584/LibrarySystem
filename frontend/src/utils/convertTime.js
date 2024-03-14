export default function(timestamp) {
    if(timestamp == null) return null;
    else return new Date(timestamp).toLocaleString("zh-TW");
}