#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>

#define SYSFS_PATH "/sys/kernel/sykt_sysfs/"
#define DSLAPA_PATH SYSFS_PATH "dslapa"
#define DTLAPA_PATH SYSFS_PATH "dtlapa"
#define DCLAPA_PATH SYSFS_PATH "dclapa"
#define DRLAPA_PATH SYSFS_PATH "drlapa"
#define MAX_BUFFER 1024

void write_sysfs(const char *path, unsigned int value) {
    int fd = open(path, O_WRONLY);
    if (fd < 0) {
        fprintf(stderr, "Error opening %s for write: %s\n", path, strerror(errno));
        exit(EXIT_FAILURE);
    }

    char buffer[MAX_BUFFER];
    int len = snprintf(buffer, sizeof(buffer), "%u", value);
    if (len < 0 || len >= MAX_BUFFER) {
        fprintf(stderr, "Buffer formatting error for write\n");
        close(fd);
        exit(EXIT_FAILURE);
    }

    if (lseek(fd, 0L, SEEK_SET) < 0) {
        perror("lseek (write)");
        close(fd);
        exit(EXIT_FAILURE);
    }

    ssize_t n = write(fd, buffer, len);
    if (n != len) {
        fprintf(stderr, "Write error to %s: wrote %zd, expected %d\n", path, n, len);
        close(fd);
        exit(EXIT_FAILURE);
    }

    printf("%s: write value %03o (hex: %#04x)\n", path, value, value);
    close(fd);
}

unsigned int read_sysfs(const char *path) {
    int fd = open(path, O_RDONLY);
    if (fd < 0) {
        fprintf(stderr, "Error opening %s for read: %s\n", path, strerror(errno));
        exit(EXIT_FAILURE);
    }

    char buffer[MAX_BUFFER];
    if (lseek(fd, 0L, SEEK_SET) < 0) {
        perror("lseek (read)");
        close(fd);
        exit(EXIT_FAILURE);
    }

    ssize_t n = read(fd, buffer, sizeof(buffer) - 1);
    if (n < 0) {
        perror("Read failed");
        close(fd);
        exit(EXIT_FAILURE);
    }

    buffer[n] = '\0';
    close(fd);

    unsigned int value = strtoul(buffer, NULL, 0);
    printf("%s: read value %03o (hex: %#04x)\n", path, value, value);
    return value;
}

int main() {
    printf("SYKOM test start (build: %s %s)\n", __DATE__, __TIME__);

    // --- Zestaw 1 ---
    unsigned int state = read_sysfs(DCLAPA_PATH);

    write_sysfs(DSLAPA_PATH, 0356);   // 0xEE
    write_sysfs(DSLAPA_PATH, 0273);   // 0xBB

    state = read_sysfs(DCLAPA_PATH);
    usleep(100000);
    state = read_sysfs(DCLAPA_PATH);

    write_sysfs(DTLAPA_PATH, 013);    // Start obliczeń (0x0B)

    state = read_sysfs(DCLAPA_PATH);
    usleep(100000);
    state = read_sysfs(DCLAPA_PATH);

    unsigned int result = read_sysfs(DRLAPA_PATH);
    printf("RESULT: %03o (hex: %#04x)\n", result, result);

    state = read_sysfs(DCLAPA_PATH);
    usleep(100000);
    state = read_sysfs(DCLAPA_PATH);

    write_sysfs(DTLAPA_PATH, 014);  // Reset kontrolera (0x0C)

    // --- Zestaw 2 ---
    write_sysfs(DSLAPA_PATH, 021);  // 0x11
    usleep(50000);
    write_sysfs(DTLAPA_PATH, 012);  // Reset bufora (0x0A)
    usleep(50000);

    write_sysfs(DTLAPA_PATH, 014);  // Reset indeksu (0x0C)
    usleep(50000);

    write_sysfs(DSLAPA_PATH, 042);  // 0x22
    usleep(50000);

    write_sysfs(DTLAPA_PATH, 012);  // Reset bufora (0x0A)
    usleep(50000);

    write_sysfs(DTLAPA_PATH, 014);  // Reset indeksu (0x0C)
    usleep(50000);

    write_sysfs(DSLAPA_PATH, 063);  // 0x33
    usleep(50000);

    write_sysfs(DTLAPA_PATH, 013);  // Start obliczeń (0x0B)
    usleep(50000);

    state = read_sysfs(DCLAPA_PATH);
    usleep(50000);
    state = read_sysfs(DCLAPA_PATH);
    usleep(100000);

    result = read_sysfs(DRLAPA_PATH);
    printf("RESULT: %03o (hex: %#04x)\n", result, result);

    // Sprawdzenie pełności danych
    write_sysfs(DTLAPA_PATH, 015); // 0x0D
    usleep(50000);
    state = read_sysfs(DCLAPA_PATH);
    usleep(50000);

    write_sysfs(DSLAPA_PATH, 021);  // Próba zapisu do pełnego bufora (0x11)
    usleep(50000);
    state = read_sysfs(DCLAPA_PATH);
    usleep(50000);
    state = read_sysfs(DCLAPA_PATH);

    write_sysfs(DTLAPA_PATH, 012);  // Reset bufora (0x0A)
    usleep(100000);

    write_sysfs(DTLAPA_PATH, 014);  // Reset indeksu (0x0C)
    usleep(100000);
    state = read_sysfs(DCLAPA_PATH);
    usleep(50000);

    printf("\nTest: Odczyt z nieistniejącego pliku\n");
    read_sysfs(SYSFS_PATH "jakassciezka");

    printf("\nTest: Zapis do nieistniejącego pliku\n");
    write_sysfs(SYSFS_PATH "notapath", 0123);


    printf("SYKOM test end\n");

    return 0;
}
