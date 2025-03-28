## Music Player App
Музыкальное приложение состоит из экрана локальных треков на устройстве (LocalTracks) и экрана треков, полученных через DeezerAPI (ApiTracks).
Экран воспроизведения трека (Playback) позволяет управлять выбранным треком из списка. 
Все три экрана реализованы по функционалу в полном объеме, также реализован поиск по обоим спискам через SearchBar. Навигация через BottomNavigation.
## Архитектура 
MVVM
## Библиотеки 
Compose, Compose Navigation, Coil, Gson, Retrofit, Kotlin Coroutines/Flow, Media3
## Инструкция по запуску
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/kimmikind/MusicApp.git
3. Откройте проект в Android Studio.
4. Запустите приложение на эмуляторе или устройстве.

- **Система сборки**: Gradle (Kotlin DSL)
- **Версия Gradle**: 8.9
- **Версия Android Gradle Plugin**: 8.7.3
- **Целевая версия SDK**: Android 14 (API 34)

Ссылка на apk-файл на диске: https://disk.yandex.ru/d/QCH7xAJawK8l2g
