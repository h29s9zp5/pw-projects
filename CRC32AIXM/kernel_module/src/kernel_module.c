#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/ioport.h>
#include <asm/uaccess.h>
#include <asm/errno.h>
#include <asm/io.h>

MODULE_INFO(intree, "Y");
MODULE_LICENSE("GPL");
MODULE_AUTHOR("Aleksander Pruszkowski");
MODULE_DESCRIPTION("Kernel module for SYKOM Project");
MODULE_VERSION("0.01");

#define SYKT_GPIO_BASE_ADDR     (0x00100000)
#define SYKT_GPIO_SIZE          (0x8000)
#define SYKT_EXIT               (0x3333)
#define SYKT_EXIT_CODE          (0x7F)

#define SYKT_GPIO_IN_OFFSET     0x00000680
#define SYKT_GPIO_STATE_OFFSET  0x00000688
#define SYKT_GPIO_CTRL_OFFSET   0x00000698
#define SYKT_GPIO_RESULT_OFFSET 0x00000690

static void __iomem *baseptr;

static ssize_t dslapa_store(struct kobject *kobj, struct kobj_attribute *attr, const char *buf, size_t count) {
    u8 wrt_dslapa;
    if (kstrtou8(buf, 0, &wrt_dslapa))
        return -EINVAL;
    printk(KERN_INFO "dslapa: write value %#o \n", wrt_dslapa, wrt_dslapa);
    writeb(wrt_dslapa, (void __iomem *)(baseptr + SYKT_GPIO_IN_OFFSET));
    return count;
}
static struct kobj_attribute dslapa_attr = __ATTR_WO(dslapa);

static ssize_t dtlapa_store(struct kobject *kobj, struct kobj_attribute *attr, const char *buf, size_t count) {
    u8 wrt_dtlapa;
    if (kstrtou8(buf, 0, &wrt_dtlapa))
        return -EINVAL;
    printk(KERN_INFO "dtlapa: write value %#o \n", wrt_dtlapa, wrt_dtlapa);
    writeb(wrt_dtlapa, (void __iomem *)(baseptr + SYKT_GPIO_CTRL_OFFSET));
    return count;
}
static struct kobj_attribute dtlapa_attr = __ATTR_WO(dtlapa);

static ssize_t dclapa_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf) {
    u32 rd_dclapa = readb((void __iomem *)(baseptr + SYKT_GPIO_STATE_OFFSET));
    printk(KERN_INFO "dclapa: read value %#o\n", rd_dclapa);
    return sprintf(buf, "%#o\n", rd_dclapa);
}
static struct kobj_attribute dclapa_attr = __ATTR_RO(dclapa);

static ssize_t drlapa_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf) {
    u32 rd_drlapa = readl((void __iomem *)(baseptr + SYKT_GPIO_RESULT_OFFSET));
    printk(KERN_INFO "drlapa: read value %#o\n", rd_drlapa);
    return sprintf(buf, "%#o\n", rd_drlapa);
}
static struct kobj_attribute drlapa_attr = __ATTR_RO(drlapa);

static struct kobject *kobj_ref;

static int __init my_module_init(void) {
    printk(KERN_INFO "Init my module.\n");

    baseptr = ioremap(SYKT_GPIO_BASE_ADDR, SYKT_GPIO_SIZE);
    if (!baseptr)
        return -ENOMEM;

    kobj_ref = kobject_create_and_add("sykt_sysfs", kernel_kobj);
    if (!kobj_ref) {
        iounmap(baseptr);
        return -ENOMEM;
    }

    if (sysfs_create_file(kobj_ref, &dslapa_attr.attr)) {
        kobject_put(kobj_ref);
        iounmap(baseptr);
        return -EINVAL;
    }

    if (sysfs_create_file(kobj_ref, &dtlapa_attr.attr)) {
        sysfs_remove_file(kobj_ref, &dslapa_attr.attr);
        kobject_put(kobj_ref);
        iounmap(baseptr);
        return -EINVAL;
    }

    if (sysfs_create_file(kobj_ref, &dclapa_attr.attr)) {
        sysfs_remove_file(kobj_ref, &dtlapa_attr.attr);
        sysfs_remove_file(kobj_ref, &dslapa_attr.attr);
        kobject_put(kobj_ref);
        iounmap(baseptr);
        return -EINVAL;
    }

    if (sysfs_create_file(kobj_ref, &drlapa_attr.attr)) {
        sysfs_remove_file(kobj_ref, &dclapa_attr.attr);
        sysfs_remove_file(kobj_ref, &dtlapa_attr.attr);
        sysfs_remove_file(kobj_ref, &dslapa_attr.attr);
        kobject_put(kobj_ref);
        iounmap(baseptr);
        return -EINVAL;
    }

    return 0;
}


static void __exit my_cleanup_module(void) {
    printk(KERN_INFO "Cleanup my module.\n");

    writel(SYKT_EXIT | (SYKT_EXIT_CODE << 16), baseptr);

    sysfs_remove_file(kobj_ref, &dslapa_attr.attr);
    sysfs_remove_file(kobj_ref, &dtlapa_attr.attr);
    sysfs_remove_file(kobj_ref, &dclapa_attr.attr);
    sysfs_remove_file(kobj_ref, &drlapa_attr.attr);
    kobject_put(kobj_ref);

	iounmap(baseptr);
}

module_init(my_module_init);
module_exit(my_cleanup_module);
