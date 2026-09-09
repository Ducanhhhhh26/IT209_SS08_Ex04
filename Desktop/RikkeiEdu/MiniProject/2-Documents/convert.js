const fs = require('fs');
const path = require('path');
const https = require('https');
const { marked } = require('marked');

// Đọc file Markdown
const mdPath = path.join(__dirname, 'System_Design_SRS.md');
let mdContent = fs.readFileSync(mdPath, 'utf8');

// Hàm mã hóa Mermaid sang URL mermaid.ink an toàn (URL-safe Base64)
function convertMermaidToUrl(mermaidCode) {
    const cleanCode = mermaidCode.trim();
    const obj = {
        code: cleanCode,
        mermaid: { theme: "default" }
    };
    
    const jsonStr = JSON.stringify(obj);
    
    // Mã hóa base64 UTF-8
    let base64 = Buffer.from(jsonStr, 'utf8').toString('base64');
    
    // Chuyển đổi sang URL-safe Base64 để tránh lỗi đường dẫn URL của mermaid.ink
    base64 = base64
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=+$/, '');
        
    return `https://mermaid.ink/img/${base64}`;
}

// Hàm tải hình ảnh từ URL và chuyển thành chuỗi Base64 Data URI
function downloadImageAsBase64(url) {
    return new Promise((resolve, reject) => {
        https.get(url, (res) => {
            if (res.statusCode !== 200) {
                reject(new Error(`Tải ảnh thất bại với mã trạng thái: ${res.statusCode} cho URL: ${url}`));
                return;
            }
            const data = [];
            res.on('data', (chunk) => {
                data.push(chunk);
            });
            res.on('end', () => {
                const buffer = Buffer.concat(data);
                const base64Image = buffer.toString('base64');
                resolve(`data:image/png;base64,${base64Image}`);
            });
        }).on('error', (err) => {
            reject(err);
        });
    });
}

// Hàm chạy tiến trình xử lý chính
async function main() {
    console.log('Đang phân tích các sơ đồ Mermaid...');
    
    // Tìm các khối Mermaid
    const mermaidRegex = /```mermaid([\s\S]*?)```/g;
    const matches = [];
    let match;
    
    while ((match = mermaidRegex.exec(mdContent)) !== null) {
        matches.push({
            fullMatch: match[0],
            code: match[1]
        });
    }
    
    // Tải và thay thế từng sơ đồ một
    for (let i = 0; i < matches.length; i++) {
        const item = matches[i];
        console.log(`Đang tải ảnh sơ đồ số ${i + 1}/${matches.length} từ mermaid.ink...`);
        try {
            const imgUrl = convertMermaidToUrl(item.code);
            const base64DataUri = await downloadImageAsBase64(imgUrl);
            mdContent = mdContent.replace(item.fullMatch, 
                `<div align="center" class="center"><img src="${base64DataUri}" alt="Sơ đồ hệ thống ${i + 1}" style="max-width: 100%; border: 1px solid #ddd; margin: 20px 0;" /></div>`
            );
            console.log(`Đã tải và nhúng thành công sơ đồ số ${i + 1}.`);
        } catch (error) {
            console.error(`Lỗi khi xử lý sơ đồ số ${i + 1}:`, error.message);
            // Nếu lỗi tải ảnh, giữ nguyên link online làm phương án dự phòng
            const fallbackUrl = convertMermaidToUrl(item.code);
            mdContent = mdContent.replace(item.fullMatch, 
                `<div align="center" class="center"><img src="${fallbackUrl}" alt="Sơ đồ hệ thống" style="max-width: 100%; border: 1px solid #ddd; margin: 20px 0;" /></div>`
            );
        }
    }

    console.log('Đang chuyển đổi Markdown sang HTML...');
    // Chuyển Markdown sang HTML
    const htmlBody = marked(mdContent);

    // Bọc trong khung HTML tương thích Microsoft Word
    const wordHtml = `
<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:w="urn:schemas-microsoft-com:office:word" xmlns="http://www.w3.org/TR/REC-html40">
<head>
<meta charset="utf-8">
<title>Software Requirements Specification (SRS) - PRJ-TECHNOVA-SHOPAI</title>
<!--[if gte mso 9]>
<xml>
<w:WordDocument>
<w:View>Print</w:View>
<w:Zoom>100</w:Zoom>
<w:DoNotOptimizeForBrowser/>
</w:WordDocument>
</xml>
<![endif]-->
<style>
@page {
    size: A4;
    margin: 1in 1in 1in 1in;
    mso-header-margin: .5in;
    mso-footer-margin: .5in;
}
body {
    font-family: "Calibri", "Arial", sans-serif;
    font-size: 11pt;
    line-height: 1.5;
    color: #333333;
}
h1 {
    font-family: "Calibri Light", "Arial", sans-serif;
    font-size: 20pt;
    color: #1F4E79;
    border-bottom: 2px solid #1F4E79;
    padding-bottom: 5px;
    margin-top: 24pt;
    margin-bottom: 12pt;
    page-break-after: avoid;
}
h2 {
    font-family: "Calibri Light", "Arial", sans-serif;
    font-size: 16pt;
    color: #2F5597;
    margin-top: 18pt;
    margin-bottom: 8pt;
    page-break-after: avoid;
}
h3 {
    font-family: "Calibri Light", "Arial", sans-serif;
    font-size: 13pt;
    color: #203764;
    margin-top: 14pt;
    margin-bottom: 6pt;
    page-break-after: avoid;
}
p {
    margin-top: 0;
    margin-bottom: 6pt;
}
ul, ol {
    margin-top: 0;
    margin-bottom: 6pt;
    padding-left: 20pt;
}
li {
    margin-bottom: 3pt;
}
table {
    border-collapse: collapse;
    width: 100%;
    margin-top: 12pt;
    margin-bottom: 12pt;
    mso-table-lspace: 0pt;
    mso-table-rspace: 0pt;
}
th {
    background-color: #1F4E79;
    color: #FFFFFF;
    font-weight: bold;
    border: 1px solid #D3D3D3;
    padding: 6px 10px;
    text-align: left;
}
td {
    border: 1px solid #D3D3D3;
    padding: 6px 10px;
    text-align: left;
}
tr:nth-child(even) td {
    background-color: #F2F4F7;
}
code {
    font-family: "Consolas", "Courier New", monospace;
    font-size: 10pt;
    background-color: #F2F2F2;
    padding: 2px 4px;
}
pre {
    font-family: "Consolas", "Courier New", monospace;
    font-size: 10pt;
    background-color: #F2F2F2;
    padding: 10px;
    border: 1px solid #D3D3D3;
    margin-top: 6pt;
    margin-bottom: 6pt;
    white-space: pre-wrap;
}
img {
    max-width: 100%;
    height: auto;
}
.center {
    text-align: center;
}
</style>
</head>
<body>
${htmlBody}
</body>
</html>
`;

    // Lưu thành file Word .doc tương thích
    const docPath = path.join(__dirname, 'System_Design_SRS.doc');
    fs.writeFileSync(docPath, wordHtml, 'utf8');

    console.log('Chuyển đổi thành công! File Word đã được tạo tại:', docPath);
}

main().catch(err => {
    console.error('Lỗi nghiêm trọng trong tiến trình:', err.message);
});
