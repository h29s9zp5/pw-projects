import matplotlib.pyplot as plt
import numpy as np
from matplotlib import pyplot

plot_every = 50 #nowy wykres co 50 iteracji

#każdy punkt na siatce posiada 9 możliwych prędkości,
# w czterech kierunkach NESW, 4 po przekątnej i 1 reprezentujaca predkosc
#zerowa (podczas przeplywu predkosci przechodza do sasiednich punktow na
#siatce

#odleglosc pomiedzy dwoma dowolnymi punktami
def distance(x1, y1, x2, y2):
    return np.sqrt((x2 - x1) ** 2 + (y2 - y1) ** 2)


def main():

    # Simulation parameters
    Nx = 400  # rozdzielczosc x-dir (ilosc komorek na osi x)
    Ny = 100  # rozdzielczosc y-dir (ilos komrokrek na osi y)
    rho0 = 100  # average density
    tau = 0.53  # stala czasowa zderzen
    Nt = 3000  # number of timesteps
    plotRealTime = True  # switch on for plotting as the simulation goes along

    # Lattice speeds / weights (predkosci w kazdym kierunku na siatce)
    NL = 9
    cxs = np.array([0, 0, 1, 1, 1, 0, -1, -1, -1])
    cys = np.array([0, 1, 1, 0, -1, -1, -1, 0, 1])
    #prędkości dyskretne dla 9 wezlow
    weights = np.array([4 / 9, 1 / 9, 1 / 36, 1 / 9, 1 / 36, 1 / 9, 1 / 36, 1 / 9, 1 / 36])  # sums to 1

    # Initial Conditions - 3 wymiarowa lista (2wymiary dla wezla,
    # 1 dla predkosci kazdej z komorek
    F = np.ones((Ny, Nx, NL)) + 0.01 * np.random.rand(Ny, Nx, NL)  # ones are more stable than zeros in simulation
    F += 0.01 * np.random.randn(Ny, Nx, NL)
    F[:, :, 3] = 2.3

    # Define obstacles
    cylinder = np.full((Ny, Nx), False)

    # Obstacle 1
    for y in range(0, Ny):
        for x in range(0, Nx):
            if distance(Nx // 4, Ny // 2, x, y) < 13:
                cylinder[y, x] = True

    # Obstacle 2
    for y in range(0, Ny):
        for x in range(0, Nx):
            if distance(Nx // 2, Ny // 3, x, y) < 13:
                cylinder[y, x] = True

    # Obstacle 3
    for y in range(0, Ny):
        for x in range(0, Nx):
            if distance(3 * Nx // 4, 2 * Ny // 3, x, y) < 13:
                cylinder[y, x] = True

    for it in range(Nt):
        print(it)
    #przesuniecie siatki
        for i, cx, cy in zip(range(NL), cxs, cys):
            F[:, :, i] = np.roll(F[:, :, i], cx, axis=1)
            F[:, :, i] = np.roll(F[:, :, i], cy, axis=0)

        bndryF = F[cylinder, :]
        bndryF = bndryF[:, [0, 5, 6, 7, 8, 1, 2, 3, 4]]

        # Fluid variables
        rho = np.sum(F, 2)
        ux = np.sum(F * cxs, 2) / rho
        uy = np.sum(F * cys, 2) / rho

        F[cylinder, :] = bndryF
        ux[cylinder] = 0
        uy[cylinder] = 0

        # Collision
        Feq = np.zeros(F.shape)
        for i, cx, cy, w in zip(range(NL), cxs, cys, weights):
            Feq[:, :, i] = rho * w * (
                    1 + 3 * (cx * ux + cy * uy) + 9 * (cx * ux + cy * uy) ** 2 / 2 - 3 * (ux ** 2 + uy ** 2) / 2
            )
        F = F + -(1 / tau) * (F - Feq)

        if it % plot_every == 0:
            pyplot.imshow(np.sqrt(ux ** 2 + uy ** 2), cmap='magma')
            pyplot.pause(.01)
            pyplot.cla()


if __name__ == "__main__":
    main()
