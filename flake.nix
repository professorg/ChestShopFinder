{
  inputs = {
    utils.url = "github:numtide/flake-utils";
  };
  outputs = { self, nixpkgs, utils }: utils.lib.eachDefaultSystem (system:
    let
      pkgs = nixpkgs.legacyPackages.${system};
      runtimeLibs = with pkgs; [
        libglvnd

        (lib.getLib stdenv.cc.cc)
        ## native versions
        glfw3-minecraft
        openal

        ## openal
        alsa-lib
        libjack2
        libpulseaudio
        pipewire

        ## glfw
        libGL
        xorg.libX11
        xorg.libXcursor
        xorg.libXext
        xorg.libXrandr
        xorg.libXxf86vm

        udev # oshi

        vulkan-loader # VulkanMod's lwjgl
      ];
    in
    {
      devShell = pkgs.mkShell {
        buildInputs = with pkgs; [
          xorg.xrandr
        ];

        LD_LIBRARY_PATH = "${pkgs.addDriverRunpath.driverLink}/lib:${nixpkgs.lib.makeLibraryPath runtimeLibs}";
      };
    }
  );
}
