# Adam Asmaca Oyunu

![Java](https://img.shields.io/badge/Java-17-orange)
![Swing](https://img.shields.io/badge/Swing-GUI-blue)
![Maven](https://img.shields.io/badge/Maven-Project-red)

## Proje Hakkında

Bu proje Java Swing kullanılarak geliştirilmiş klasik "Adam Asmaca" oyunudur. Oyun çeşitli özelliklerle donatılmıştır.

## Özellikler

### 🔐 Güvenlik
- **Şifre Sistemi**: İlk çalıştırmada şifre belirleme, sonraki girişlerde şifre kontrolü
- **3 Deneme Hakkı**: 3 kez hatalı şifre girilirse program kapanır
- **Log Kaydı**: Tüm giriş ve şifre denemeleri log dosyasına kaydedilir

### 🎮 Oyun Özellikleri
- **Rastgele Kelime Seçimi**: En az 6 harfli 30 kelimeden rastgele seçim
- **Harf Tahmini**: Tek tek harf tahmini yapabilme
- **Kelime Tahmini**: Tüm kelimeyi tahmin edebilme
- **Görsel İlerleme**: 11 adım adam resmi ile yanlış tahminleri görselleştirme
- **Süre Sayacı**: Oyun süresini saniye olarak takip etme
- **Oyun Kayıtları**: Tüm oyun sonuçlar kaydedilir

### Proje Yapısı
```
adam_asmaca/
├── src/
│   └── main/
│       └── java/
│           └── com/mycompany/hangman/
│               ├── Main.java
│               ├── config/
│               │   └── Constants.java
│               └── view/
│                   ├── AuthFrame.java
│                   ├── MainFrame.java
│                   ├── GamePanel.java
│                   ├── ScorePanel.java
│                   └── LogPanel.java
├── pom.xml
└── README.md
```

## Geliştirici Notları

### Kod Yapısı
- **Constants.java**: Tüm dosya yolları ve sabitler burada tanımlanmıştır
- **AuthFrame.java**: Şifre giriş ve belirleme ekranı
- **MainFrame.java**: Ana pencere, menü ve tab yapısı
- **GamePanel.java**: Oyun mantığı ve arayüzü
- **ScorePanel.java**: Oyun sonuçlarını görüntüleme
- **LogPanel.java**: Logları görüntüleme

### Özelleştirme
- Kelimeleri değiştirmek için `C:\P2Oyun\TXTDosyalar\kelimeler.txt` dosyasını düzenleyin
- Her satıra en az 6 harfli bir kelime ekleyin
- Resimleri değiştirmek için `C:\P2Oyun\Resimler\` klasörüne yeni resimler koyun
