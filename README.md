# Social Media Content Downloader Bot

This Java-based Telegram bot enables users to download videos and images from Instagram, TikTok, and Pinterest by providing the respective media URLs.

## Features

- **Instagram Downloads**: Retrieve videos, reels, and images from Instagram posts using the integrated [InstagramDownloaderApi](https://github.com/Fayzulloh2024/InstagramDownloaderApi).
- **TikTok Downloads**: Download videos from TikTok by processing shared URLs.
- **Pinterest Downloads**: Save images and videos from Pinterest pins.

## Prerequisites

- **Java Development Kit (JDK)**: Ensure JDK 8 or higher is installed.
- **Telegram Bot API Token**: Obtain a bot token from [BotFather](https://core.telegram.org/bots#botfather).
- **Python Environment**: Required to run the InstagramDownloaderApi.
- **Dependencies**:
  - [TelegramBots Java Library](https://github.com/rubenlagus/TelegramBots)
  - [Apache HttpClient](https://hc.apache.org/httpcomponents-client-5.0.x/index.html) for HTTP requests
  - [InstagramDownloaderApi](https://github.com/Fayzulloh2024/InstagramDownloaderApi) for Instagram content retrieval

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/SocialMediaDownloaderBot.git
   ```

2. **Navigate to the Project Directory**:
   ```bash
   cd SocialMediaDownloaderBot
   ```

3. **Configure the Bot**:
   - Open the `config.properties` file.
   - Set your Telegram Bot API token:
     ```
     bot.token=YOUR_TELEGRAM_BOT_TOKEN
     ```

4. **Set Up InstagramDownloaderApi**:
   - Clone the InstagramDownloaderApi repository:
     ```bash
     git clone https://github.com/Fayzulloh2024/InstagramDownloaderApi.git
     ```
   - Navigate to its directory:
     ```bash
     cd InstagramDownloaderApi
     ```
   - Install the required Python packages:
     ```bash
     pip install -r requirements.txt
     ```
   - Run the API server:
     ```bash
     python app.py
     ```
     By default, the API will run on `http://localhost:8000`.

5. **Build the Java Project**:
   - If using Maven:
     ```bash
     mvn clean package
     ```
   - If using Gradle:
     ```bash
     gradle build
     ```

6. **Run the Bot**:
   ```bash
   java -jar target/SocialMediaDownloaderBot.jar
   ```

## Usage

1. **Start a Chat**: Open Telegram and start a conversation with your bot.
2. **Send a Media URL**: Paste the URL of the Instagram, TikTok, or Pinterest media you wish to download.
3. **Receive the Media**: The bot will process the URL and send the media file back to you.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your enhancements.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

---

This README provides a comprehensive guide to setting up and using the Java-based Telegram bot for downloading media from Instagram, TikTok, and Pinterest. 
